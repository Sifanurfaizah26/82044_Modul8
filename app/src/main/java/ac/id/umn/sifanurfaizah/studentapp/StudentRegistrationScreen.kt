package ac.id.umn.sifanurfaizah.studentapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel = viewModel()) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    var currentPhone by remember { mutableStateOf("") }
    var phoneList by remember { mutableStateOf(listOf<Phone>()) }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
    ) {
        Text("Register New Student", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID") })
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        TextField(value = program, onValueChange = { program = it }, label = { Text("Program") })

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = currentPhone,
                onValueChange = { currentPhone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (currentPhone.isNotBlank()) {
                    phoneList = phoneList + Phone(number = currentPhone)
                    currentPhone = ""
                }
            }, modifier = Modifier.padding(start = 8.dp)) {
                Text("Add")
            }
        }

        if (phoneList.isNotEmpty()) {
            Column {
                Text("Phone Numbers:", style = MaterialTheme.typography.labelLarge)
                phoneList.forEachIndexed { index, phone ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("- ${phone.number}", modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            phoneList = phoneList.filterIndexed { i, _ -> i != index }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Phone")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (studentId.isNotBlank() && name.isNotBlank() && program.isNotBlank()) {
                    viewModel.addStudent(Student(studentId, name, program, phoneList))
                    studentId = ""
                    name = ""
                    program = ""
                    phoneList = listOf()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Student")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Student List", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        val students by viewModel.students.collectAsState()

        LazyColumn {
            items(students) { student ->
                StudentItem(student = student, viewModel = viewModel)
                Divider()
            }
        }

    }
}

@Composable
fun StudentItem(student: Student, viewModel: StudentViewModel) {
    var editName by remember { mutableStateOf(student.name) }
    var editProgram by remember { mutableStateOf(student.program) }
    var isEditing by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)) {
        if (isEditing) {
            TextField(value = editName, onValueChange = { editName = it }, label = { Text("Edit Name") })
            TextField(value = editProgram, onValueChange = { editProgram = it }, label = { Text("Edit Program") })
            Row {
                Button(onClick = {
                    viewModel.updateStudent(student.id, editName, editProgram)
                    isEditing = false
                }) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { isEditing = false }) {
                    Text("Cancel")
                }
            }
        } else {
            Text("ID: ${student.id}")
            Text("Name: ${student.name}")
            Text("Program: ${student.program}")

            Spacer(modifier = Modifier.height(4.dp))

            if (student.phones.isNotEmpty()) {
                Text("Phones:")
                student.phones.forEach { phone ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("- ${phone.number}", modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            viewModel.deletePhone(student.id, phone.id)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Phone")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Button(onClick = { isEditing = true }) {
                    Text("Edit Student")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.deleteStudent(student.id)
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Delete Student")
                }
            }
        }
    }
}
