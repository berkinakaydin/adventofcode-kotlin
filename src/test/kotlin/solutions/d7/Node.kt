package solutions.d7

class Node(
    val name: String, val parent: Node?
) {
    val data = ArrayList<File>()
    val childNodes = ArrayList<Node>()
    var size = 0
}