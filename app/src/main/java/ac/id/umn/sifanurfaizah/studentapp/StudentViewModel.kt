package ac.id.umn.sifanurfaizah.studentapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
class StudentViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    init {
        fetchStudents()
    }

    fun fetchStudents() {
        db.collection("students")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val studentList = snapshot.documents.map { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val program = doc.getString("program") ?: ""

                    Student(id = id, name = name, program = program)
                }
                _students.value = studentList

                studentList.forEach { student ->
                    fetchPhonesForStudent(student.id)
                }
            }
    }

    private fun fetchPhonesForStudent(studentId: String) {
        db.collection("students").document(studentId)
            .collection("phones")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val phones = snapshot.documents.map { doc ->
                    Phone(id = doc.id, number = doc.getString("number") ?: "")
                }

                _students.value = _students.value.map { student ->
                    if (student.id == studentId) student.copy(phones = phones) else student
                }
            }
    }

    fun addStudent(student: Student) {
        val studentData = mapOf(
            "name" to student.name,
            "program" to student.program
        )

        db.collection("students").document(student.id)
            .set(studentData)
            .addOnSuccessListener {
                student.phones.forEach { phone ->
                    addPhone(student.id, phone.number)
                }
            }
    }

    fun addPhone(studentId: String, number: String) {
        val phoneData = mapOf("number" to number)
        db.collection("students").document(studentId)
            .collection("phones")
            .add(phoneData)
    }

    fun updateStudent(studentId: String, name: String, program: String) {
        val updates = mapOf(
            "name" to name,
            "program" to program
        )
        db.collection("students").document(studentId)
            .update(updates)
    }

    fun updatePhone(studentId: String, phoneId: String, newNumber: String) {
        db.collection("students").document(studentId)
            .collection("phones")
            .document(phoneId)
            .update("number", newNumber)
    }

    fun deleteStudent(studentId: String) {
        val studentRef = db.collection("students").document(studentId)

        studentRef.collection("phones")
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.delete()
                }
                studentRef.delete()
            }
    }

    fun deletePhone(studentId: String, phoneId: String) {
        db.collection("students").document(studentId)
            .collection("phones")
            .document(phoneId)
            .delete()
    }
}
