package org.itc.com
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object DFWordCount {
  def main(args: Array[String]): Unit = {
    val sparkconf=new SparkConf()
    sparkconf.set("spark.app.name","firstDFDemo")
    sparkconf.set("spark.master","local[1]")
    val spark=SparkSession.builder().config(sparkconf).getOrCreate()

    // Read the input text file into a DataFrame
    val orderSchemaddl= "orderstatus String,orderid Int,orderdate String,custid Int"

    //spark.read.option("header", "true").option("inferSchema",true).csv("")

    val ordersdf = spark.read.option("header", "true").schema(orderSchemaddl).csv("C:\\Users\\deepu\\Scala Project\\Input\\orders2.csv")

    // Split each line into words and explode the resulting array of words into separate rows
    val wordsDF = ordersdf.select(explode(split(col("orderstatus"), "\\s+")).as("word"))

    // Group by word and count the occurrences of each word
    val wordCountsDF = wordsDF.groupBy("word").count()

    // Show the word counts
    wordCountsDF.show()

    /*wordCountsDF.write
     .format("csv")
     .option("header", "true")
     .save("C:\\Users\\deepu\\Scala Project\\Output\\DFWordCount_output")
      // Stop the SparkSession
    spark.stop()
    */

    wordCountsDF.coalesce(numPartitions=1).write.csv("C:\\Users\\deepu\\Scala Project\\Output\\DFWordCount_output")

  }
}
