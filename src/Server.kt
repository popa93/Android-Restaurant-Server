import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

import java.net.ServerSocket
import java.net.Socket
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.StringBuilder

class Server {

    private var running:Boolean=true;
    lateinit var serverSocket: ServerSocket
    lateinit var socket: Socket
    lateinit var message:String
    var dbHandler=DBHandler()
    var orderList= ArrayList<String>()
    lateinit var email:String
    lateinit var totalPrice:String
    lateinit var foodString:String
    lateinit var drinkString:String
    var result:Int = 0


    fun receiveAndWriteDataToDB() {

        serverSocket= ServerSocket(3001)
        while(running){

            socket=serverSocket.accept()
            dbHandler.createOrConnectDB()

            Thread {

                var reader = InputStreamReader(socket.getInputStream())
                var bufferReader = BufferedReader(reader)
                message = bufferReader.readLine()
                orderList = stringTokenizer(message)//arrayof products
                email=emailString(orderList)
                totalPrice=totalPriceString(orderList)
                foodString=foodString(orderList)
                drinkString=drinkString(orderList)

                result=dbHandler.writeToDB(email,totalPrice,foodString,drinkString)//must syncronize db in order to eliminate conflicts
                dbHandler.disconnetFromDB()

                postOrder(orderList)
                sendData(socket,result)

            }.start()

        }
    }

    fun postOrder(orderList: ArrayList<String>){

        println("Order from "+email+":")
        println("Food:   "+foodString)
        println("Drink:   "+drinkString)
    }

    fun noOfOrderListItems():Int{

        return  orderList.size-5
    }

    fun waitTime():Int{
        var items=noOfOrderListItems()

        if(items<=2)
            return 4
        else if(items>2&&items<=4)
            return 8
        else if(items>4&& items<=8)
            return 13
        else
            return 18

    }

    fun sendData(socket: Socket,result:Int){

        var printWriter=PrintWriter(socket.getOutputStream())

        var stringBuilder=StringBuilder()
        stringBuilder.append(result)
        stringBuilder.append(" ")
        stringBuilder.append(waitTime())
        printWriter.write(stringBuilder.toString())
        printWriter.flush()
        printWriter.close()
        socket.close()
    }

    fun drinkString(orderList:ArrayList<String>):String{
        var drinkString= StringBuilder()

        for(i in orderList.size-2 downTo  0 step 1){

            if(orderList.get(i).equals("drinks")) {
                break;
            }
            else if(orderList.get(i).equals("total")) {
                continue
            }
            else{
                drinkString.append(orderList.get(i))
                drinkString.append(" ")
            }
        }

        return drinkString.toString()

    }

    fun foodString(orderList:ArrayList<String>):String{
        var foodString= StringBuilder()

        for(i in 1 until orderList.size step 1){

            if(orderList.get(i).equals("food")) {
                continue
            }
            else if(orderList.get(i).equals("drinks")) {
                break
            }
            else if(orderList.get(i).equals("total")) {
                break
            }
            else{
                foodString.append(orderList.get(i))
                foodString.append(" ")
            }
        }

            return foodString.toString()

    }

    fun emailString(orderList:ArrayList<String>):String{
        var email:String

        email=orderList.get(0)
        return email


    }

    fun totalPriceString(orderList:ArrayList<String>):String{
        var totalPrice:String

        totalPrice=orderList.get(orderList.lastIndex)
        return totalPrice
    }

    fun stringTokenizer(message:String):ArrayList<String>{
         var orderList= ArrayList<String>()

        var tokenizer = StringTokenizer(message)

        while(tokenizer.hasMoreTokens()){
            orderList.add(tokenizer.nextToken())
        }

        return  orderList
    }
}