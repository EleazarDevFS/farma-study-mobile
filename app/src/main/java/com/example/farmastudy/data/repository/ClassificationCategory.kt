package com.example.farmastudy.data.repository

enum class ClassificationCategory (val route: String, val label: String){
    THERAPEUTIC_USE("therapeutic_use", "Uso terapéutico"),
    MECHANISM("mechanism", "Mecanismo de acción"),
    CHEMICAL_STRUCTURE("chemical_structure", "Estructura química"),
    ORGANIC_SYSTEM("organic_system", "Sistema orgánico")

}