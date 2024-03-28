import mysql.connector as conn
import pandas as pd

# Connect to MySQL server
mydb = conn.connect(host="localhost", user="root", password="Kittians@01", database="country1")

# Create a cursor object
cursor = mydb.cursor()

# Read the CSV file into a pandas DataFrame
csv_file_path = r'C:\Users\deepu\Desktop\PythonPracticeDocs\country1.csv'
df = pd.read_csv(csv_file_path)

# Replace 'nan' values with None
df = df.where(pd.notnull(df), None)

# Insert data from DataFrame into the 'country1' table
for _, row in df.iterrows():
    country = row['country']
    country_code = row['country_code']
    continent = row['continent']
    population = row['population']

    # SQL query to insert data into the 'country1' table
    insert_query = "INSERT INTO country1 (country, country_code, continent, population) VALUES (%s, %s, %s, %s)"
    values = (country, country_code, continent, population)

    # Execute the SQL query
    cursor.execute(insert_query, values)

# Commit the transaction
mydb.commit()

# Close cursor and connection
cursor.close()
mydb.close()

print("Data inserted into 'country1' table successfully.")
