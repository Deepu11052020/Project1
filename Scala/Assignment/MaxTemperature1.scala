package org.itc.com

import scala.io.Source

object MaxTemperature1 {
  def main(args: Array[String]): Unit = {
    // Input file path
    val inputFilePath = "C:\\Users\\deepu\\Scala Project\\Deepu_Mar2024\\src\\main\\scala\\Temp.txt"

    // Read the content of the input file
    val lines = Source.fromFile(inputFilePath).getLines()

    // Initialize max temperature variable
    var maxTemperature = Double.MinValue

    // Iterate through each line to find the maximum temperature
    for (line <- lines) {
      val fields = line.split(",")
      if (fields.length == 3) {
        val temperature = fields(2).toDouble
        maxTemperature = Math.max(maxTemperature, temperature)
      }
    }

    // Print the maximum temperature
    println("Maximum temperature: " + maxTemperature)
  }
}
