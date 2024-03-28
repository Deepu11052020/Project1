import mysql.connector as conn
import pandas as pd


mydb=conn.connect(host="localhost", user="root", password="Kittians@01")
mydb
cursor=mydb.cursor()
#cursor.execute("create database country")
#cursor.execute("""create table country.population(country varchar(75),country_code varchar(75),
#continent varchar(75),population long)""")

sql = "INSERT INTO country.population (country, country_code, continent, population) VALUES (%s, %s, %s, %s)"

#mydb.close()
# Values to insert into the table
values = [
    ("USA", "US", "North America", 328_200_000),
    ("China", "CN", "Asia", 1_394_000_000),
    ("India", "IN", "Asia", 1_366_000_000),
    ("Brazil", "BR", "South America", 211_000_000),
    ("Russia", "RU", "Europe", 146_000_000)
]
# Execute the SQL statement with the values
cursor.executemany(sql, values)
#df1, = pd.read_csv("your_file.csv")
# Commit the changes


mydb.commit()


df = pd.read_sql("SELECT * FROM country.population", mydb)
print(df)



# Close the MySQL connection
mydb.close()
# Close the MySQL connection
mydb.close()

# Close cursor and database connection
#cursor.close()