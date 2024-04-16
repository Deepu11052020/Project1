package org.itc.com
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.itc.com.SparkAssignment2.menudf
import org.apache.spark.sql.types._

object SparkAssignment2 extends App {
  val sparkconf = new SparkConf()
  sparkconf.set("spark.app.name", "firstDFDemo")
  sparkconf.set("spark.master", "local[1]")
  val spark = SparkSession.builder().config(sparkconf).getOrCreate()


  val salesSchema = "customer_id string, order_date date, product_id int"
  var salesdf = spark.read.option("header", true).schema(salesSchema).csv(path = "C:\\Users\\deepu\\Scala Project\\Input\\sales.csv")
  //salesdf.show()

  val membersSchema = "customer_id string, join_date date"
  var membersdf = spark.read.option("header", true).schema(membersSchema).csv(path = "C:\\Users\\deepu\\Scala Project\\Input\\members.csv")
  //membersdf.show()

  val menuSchema = "product_id int, product_name string,price int"
  var menudf = spark.read.option("header", true).schema(menuSchema).csv(path = "C:\\Users\\deepu\\Scala Project\\Input\\menu.csv")
  //menudf.show()

  // 1. Total amount each customer spent at the restaurant
  val amountSpentPerCustomerDF = salesdf
    .join(menudf, salesdf("product_id") === menudf("product_id"))
    .groupBy("customer_id")
    .agg(sum("price").alias("total_amount_spent"))
  amountSpentPerCustomerDF.show()
  amountSpentPerCustomerDF
    .coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\AmountSpentPerCustomer_1")

  // 2. Number of days each customer visited the restaurant
  // Group by customer_id and count the distinct order_date
  val daysVisitedPerCustomerDF = salesdf
    .groupBy("customer_id")
    .agg(count("order_date").alias("Days_Visited"))

  // Show the result
  daysVisitedPerCustomerDF.show()
  daysVisitedPerCustomerDF.coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\DaysVisitedPerCustomerDF_2")

  // 3. First item purchased from the menu by each customer
  val firstItemPerCustomerDF = salesdf
    .join(menudf, salesdf("product_id") === menudf("product_id"))
    .withColumn("rank_order", rank().over(Window.partitionBy("customer_id").orderBy("order_date")))
    .filter(col("rank_order") === 1)
    .groupBy("customer_id")
    .agg(first("product_name").alias("first_item_purchased"))
    firstItemPerCustomerDF.show()
    firstItemPerCustomerDF.coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\FirstItemPerCustomerDF_3")

  // 4 What is the most purchased item on the menu and how many times was it purchased by all customers
  val mostPurchasedItemDF = salesdf
    .join(menudf, salesdf("product_id") === menudf("product_id"))
    .groupBy("product_name")
    .agg(count(menudf("product_id")).alias("purchase_count"))
    .orderBy(desc("purchase_count"))
    .limit(1)
     mostPurchasedItemDF.show()
     mostPurchasedItemDF.coalesce(numPartitions=2)
      .write
      .option("header", "true")
      .csv("C:\\Users\\deepu\\Scala Project\\Output\\MostPurchasedItemDF_4")


   //5. Most popular item for each customer
     val mostPopularItemPerCustomerDF = salesdf
    .join(menudf, salesdf("product_id") === menudf("product_id"))
    .groupBy("customer_id", "product_name")
    .agg(count(menudf("product_id")).alias("order_count"))
    .withColumn("rank_order", rank().over(Window.partitionBy("customer_id").orderBy(desc("order_count"))))
    .filter(col("rank_order") === 1)
    mostPopularItemPerCustomerDF.show()
     mostPopularItemPerCustomerDF
    .coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\MostPopularItemPerCustomerDF_5")



  // 6.Which item was purchased first by the customer after they became a member

  val firstPurchaseAfterMembershipDF = salesdf
    .join(membersdf, salesdf("customer_id") === membersdf("customer_id"), "inner")
    .join(menudf, salesdf("product_id") === menudf("product_id"), "inner")
    .filter(salesdf("order_date") >= membersdf("join_date"))
    .groupBy(salesdf("customer_id"), menudf("product_name"))
    .agg(min(salesdf("order_date")).alias("first_purchase_date"))

  firstPurchaseAfterMembershipDF.show()
  firstPurchaseAfterMembershipDF
    .coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\firstPurchaseAfterMembershipDF_6")


}
