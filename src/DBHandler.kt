import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*


class DBHandler {

    lateinit var connection:Connection
    lateinit var statement: Statement

    //also implement write to db method
    fun createOrConnectDB(){

        try {
            connection= DriverManager.getConnection("jdbc:sqlite:D:\\Facultate informatica\\Anul 2\\Sem2\\Dezvoltarea aplicatiilor mobile\\RestaurantServerKotlin\\RestaurantDB.db")
            statement =connection.createStatement()
            statement.execute("CREATE TABLE IF NOT EXISTS RestaurantDB (OrderNo INTEGER PRIMARY KEY AUTOINCREMENT, Email TEXT, Food TEXT,Drink TEXT, Total TEXT, Date TEXT)")
            //statement.execute("INSERT INTO contacts (name, phone, email) VALUES('Tim', 12323, 'tim@email.com')")    //cu jdbc face commit automat si datele salvate sunt persistente
            //statement.close()
            //connection.close()
        }catch (ex:Exception){

            println(ex.message)
        }

    }

     @Synchronized
     fun writeToDB(email:String,price:String, food:String,drinks:String):Int{

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date()).toString()

        val statement2: PreparedStatement = connection.prepareStatement(
                "INSERT INTO RestaurantDB (Email, Food, Drink, Total, Date) VALUES(?, ?, ?, ?, ?)")
        statement2.setString(1, email)
        statement2.setString(2, food)
        statement2.setString(3, drinks)
        statement2.setString(4, price)
        statement2.setString(5, currentDate)
        try {
            statement2.execute()
        }catch (ex:Exception){

            return -1
        }

        statement2.close()

        return  1
     }

    fun disconnetFromDB(){
        statement.close()
        connection.close()

    }

}