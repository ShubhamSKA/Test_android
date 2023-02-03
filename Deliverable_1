fun main() {
	val x=5
    fun spasterisk(total: Int): MutableList<String>{
        var ogast = mutableListOf("*")
        for (i in 1..(total-1)){
            ogast.add(" ")
        }
        return ogast
    }
    fun addasterisk(line: Int, astst: MutableList<String>): MutableList<String>{
        for (i in 1..(line-1)){
            astst[i]="*"
        }
        return astst
    }
    try{
    for (i in 1..x){
        println(addasterisk(i,spasterisk(x)))
    }
    } 
    catch(e: Exception){
        println("Incorrect input type")
    }
}