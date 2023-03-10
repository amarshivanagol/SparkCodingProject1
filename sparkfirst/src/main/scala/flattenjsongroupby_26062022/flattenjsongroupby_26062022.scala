package flattenjsongroupby_26062022

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import scala.io.Source

object flattenjsongroupby_26062022 {

  case class schema(txnno: String, txndate: String,
                    custno:   String,
                    amount:   String,
                    category: String,
                    product:  String,
                    city:     String,
                    state:    String,
                    spendby:  String)

  def main(args: Array[String]): Unit = {
    println("====started=====")

    val conf = new SparkConf().setAppName("first").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    val spark = SparkSession
      .builder()
      .config(conf)
      .getOrCreate()
    import spark.implicits._

    val df = spark.read.format("json")
      .option("multiLine", "true")
      .load("file:///C:/Users/002NAM744/Data/JsonData_26062022/jc9.json")

    df.show()
    df.printSchema()

    println("=====flattendf======")

    val flattendf = df.withColumn("Students", explode(col("Students")))

    val flattendf1 = flattendf.select(

      col("Students.user.*"),
      col("doorno"),
      col("orgname"),
      col("trainer"))

    flattendf1.show()
    flattendf1.printSchema()

    val complexdf = flattendf1.groupBy("doorno", "orgname", "trainer")

      .agg(

        collect_list(
          struct(
            struct(
              col("location"),
              col("name")).alias("user"))).alias("Students"))

    complexdf.show()

    complexdf.printSchema()

  }

}