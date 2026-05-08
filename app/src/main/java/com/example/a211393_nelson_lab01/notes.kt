package com.example.a211393_nelson_lab01

//MUTABLESTATEOF
/*
mutablestateof: notify when screen value changes
var points = mutableStateOf(0)
val studyItems = mutableStateListOf<StudyItem>()

- needs remember inside a composable screen(write on sand)
remember:saves value across recompositions and more like storage
mutable will fetch from remember
var docTitle by remember { mutableStateOf("")

-doesnt need remember in viewmodel(write on rock)
doesnt reset
fetches data from model so no need remember
needs mutable to update when data changes
 */

//VIEWMODEL
/*
Models.kt: has data which is like blueprint
Appviewmodel.kt:creates storage and actions
create only one viewmodel as all same info across all screens

step 1 - appnav declares viewmodel
step 2 - appvm holds all the data
step 3  - each screen calls function from appvm
 */