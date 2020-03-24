package example.kfold.muzimaintentone.data

import java.util.*

data class Person (
    var gender: MutableList<String>? = mutableListOf(),
    var name: String? = null,
    var age: Number? = null,
    var birthdate: Date? = null
){
    override fun toString(): String {
        return "Person(gender=$gender, name=$name, age=$age, birthdate=$birthdate)"
    }
}