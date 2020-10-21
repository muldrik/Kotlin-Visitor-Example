interface Visitor <T> {
    fun visitAdd(node: Add): T
    fun visitMultiply(node: Multiply): T
    fun visitLeaf(node: Leaf): T
}

/**
 * Return a readable string
 * @param autoPrint print the output if True
 */
class PrintVisitor(private val autoPrint: Boolean = false): Visitor <String>  {

    override fun visitAdd(node: Add): String {
        val result = "(${node.leftChild.accept(PrintVisitor())}+${node.rightChild.accept(PrintVisitor())})"
        if (autoPrint) println(result)
        return result
    }

    override fun visitLeaf(node: Leaf): String {
        val result = node.value.toString()
        if (autoPrint) println(result)
        return result
    }

    override fun visitMultiply(node: Multiply): String {
        val result = "${node.leftChild.accept(PrintVisitor())}*${node.rightChild.accept(PrintVisitor())}"
        if (autoPrint) println(result)
        return result
    }

}

/**
 * Calculate the expression
 */
class CalculateVisitor: Visitor<Int> {

    override fun visitAdd(node: Add): Int {
        return node.leftChild.accept(CalculateVisitor())+node.rightChild.accept(CalculateVisitor())
    }
    override fun visitLeaf(node: Leaf): Int {
        return node.value
    }
    override fun visitMultiply(node: Multiply): Int {
        return node.leftChild.accept(CalculateVisitor())*node.rightChild.accept(CalculateVisitor())
    }
}

/**
 * Apply distributivity to every multiplication
 */
class ExpandVisitor: Visitor<String> {

    override fun visitAdd(node: Add): String {
        return "${node.leftChild.accept(ExpandVisitor())}+${node.rightChild.accept(ExpandVisitor())}"
    }

    override fun visitLeaf(node: Leaf): String {
        return node.value.toString()
    }

    override fun visitMultiply(node: Multiply): String {
        var result = ""
        val leftExps = node.leftChild.accept(ExpandVisitor()).split('+')
        val rightExps = node.rightChild.accept(ExpandVisitor()).split('+')
        for (lExp in leftExps) {
            for (rExp in rightExps) {
                result+="$lExp*$rExp+"
            }
        }
        result = result.trim('+')
        return result
    }
}

interface Node {
    fun <T> accept(visitor: Visitor<T>): T
}

class Add(val leftChild: Node, val rightChild: Node): Node {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitAdd(this)
    }
}

class Multiply(val leftChild: Node, val rightChild: Node): Node {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitMultiply(this)
    }
}

/**
 * Node containing only a number
 */
class Leaf(val value: Int): Node {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitLeaf(this)
    }
}


fun main() {
    // Я не успел сделать хороший парсер, поэтому лучше так, чем если не запустится. Надеюсь, в конце концов не об этом задание
    val root = Multiply(Add(Leaf(5), Leaf(2)), Add(Multiply(Leaf(7), Leaf(11)), Leaf(2)))

    root.accept(PrintVisitor(true)) //true to print from the visitor. Can also be assigned to a variable

    val calculation = root.accept(CalculateVisitor())
    println(calculation)

    val expansion = root.accept(ExpandVisitor())
    println(expansion)
}