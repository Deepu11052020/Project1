package org.itc.com
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.itc.com.SparkAssignment2.amountSpentPerCustomerDF
import org.itc.com.SparkSQL._

object SparkAssignments3 extends App {
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

  salesdf.createOrReplaceTempView("tblSales")
  membersdf.createOrReplaceTempView("tblmembres")
  menudf.createOrReplaceTempView("tblmenu")
  //spark.sql("select * from tblSales").show()
  //spark.sql("select * from tblmemebres").show()
  //spark.sql("select * from tblmenu").show()

  //7. Which item was purchased just before the customer became a member?
  val beforeJoinDF = spark.sql(
    """      |SELECT s.customer_id,
      |       me.product_name AS product_name,
      |       MAX(s.order_date) AS last_purchase_date_before_join
      |FROM tblSales s
      |INNER JOIN tblmembres m ON s.customer_id = m.customer_id
      |INNER JOIN tblmenu me ON me.product_id = s.product_id
      |WHERE s.order_date < m.join_date
      |GROUP BY s.customer_id, me.product_name
      |ORDER BY customer_id
      |""".stripMargin)
  beforeJoinDF.show()
  beforeJoinDF
    .coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\beforeJoinDF_7")

  //8.What is the total items and amount spent for each member before they became a member?
  val amountSpentDF = spark.sql(
    """
      |SELECT s.customer_id,
      |       COUNT(*) AS total_items,
      |       SUM(m.price) AS total_amount_spent
      |FROM tblSales s
      |INNER JOIN tblmenu m ON s.product_id = m.product_id
      |INNER JOIN tblmembres mb ON s.customer_id = mb.customer_id
      |WHERE s.order_date < mb.join_date
      |GROUP BY s.customer_id
      |""".stripMargin)
  amountSpentDF.show()
  amountSpentDF
    .coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\amountSpentDF_8")

  //9 If each $1 spent equates to 10 points and sushi has a 2x points multiplier - how many points would each customer have?
  val purchasePointsDF = spark.sql(
    """|WITH PurchasePoints AS (
       |    SELECT s.customer_id,
       |        SUM(
       |            CASE
       |                WHEN m.product_name = 'sushi' THEN 20 * m.price
       |                ELSE 10 * m.price
       |            END
       |        ) AS total_points
       |    FROM tblSales s
       |    INNER JOIN tblmenu m ON s.product_id = m.product_id
       |    GROUP BY s.customer_id
       |)
       |SELECT  customer_id,COALESCE(total_points, 0) AS total_points
       |FROM
       |    PurchasePoints
       |""".stripMargin)
   purchasePointsDF.show()
  purchasePointsDF
    .coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\purchasePointsDF_9")


//10.In the first week after a customer joins the program (including their join date) they earn 2x points on all items, not just sushi
//       how many points do customer A and B have at the end of January?
val totalPointsDF = spark.sql(
  """SELECT s.customer_id,
      SUM(
          CASE
              WHEN s.order_date >= mb.join_date AND s.order_date < DATE_ADD(mb.join_date, 7) THEN 20 * m.price  -- First week after joining
              ELSE 10 * m.price
          END
      ) AS total_points
  FROM tblSales s
  INNER JOIN tblMenu m ON s.product_id = m.product_id
  INNER JOIN tblmembres mb ON s.customer_id = mb.customer_id
  WHERE s.order_date <= '2021-01-31'  -- End of January
  GROUP BY s.customer_id
  """
)
  totalPointsDF.show()
  totalPointsDF
    .coalesce(numPartitions=1)
    .write
    .option("header", "true")
    .csv("C:\\Users\\deepu\\Scala Project\\Output\\totalPointsDF_")

}