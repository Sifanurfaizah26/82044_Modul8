package ac.id.umn.sifanurfaizah.studentapp

data class Phone(
    val id: String = "",
    val number: String = ""
)

data class Student(
    val id: String = "",
    val name: String = "",
    val program: String = "",
    val phones: List<Phone> = listOf()
)
