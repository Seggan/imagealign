package io.github.seggan.imagealign

data class Vector(val x: Double, val y: Double) {

    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    operator fun plus(other: Vector): Vector {
        return Vector(this.x + other.x, this.y + other.y)
    }

    operator fun minus(other: Vector): Vector {
        return Vector(this.x - other.x, this.y - other.y)
    }

    operator fun times(scalar: Double): Vector {
        return Vector(this.x * scalar, this.y * scalar)
    }

    operator fun div(scalar: Double): Vector {
        return Vector(this.x / scalar, this.y / scalar)
    }
}