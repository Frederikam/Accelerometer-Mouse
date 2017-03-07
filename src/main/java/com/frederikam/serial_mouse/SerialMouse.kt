package com.frederikam.serial_mouse

import gnu.io.CommPortIdentifier
import gnu.io.SerialPort
import java.awt.MouseInfo
import java.awt.Robot
import java.io.IOException
import java.io.InputStream
import java.util.*


class SerialMouse {

    companion object Launcher {

        val SENSITIVITY_FORCE = 0.0005f
        val SENSITIVITY_EULER = 0.01f
        val GRAVITATIONAL_ACCELERATION = 0//16000

        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("java.library.path", System.getenv("PATH"))

            println(System.getProperty("java.library.path"))

            SerialMouse().connect("COM6")
        }
    }

    fun connect(portName: String) {
        val portIdentifier = CommPortIdentifier.getPortIdentifier(portName)
        //if (portIdentifier.isCurrentlyOwned) {
        //    println("Error: Port is currently in use")
        //} else {
            val commPort = portIdentifier.open(this.javaClass.name, 2000)

            if (commPort is SerialPort) {
                commPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)

                val `in` = commPort.inputStream

                Thread(SerialReader(`in`)).start()

            } else {
                println("Error: Only serial ports are handled by this code.")
            }
        //}
    }

    class SerialReader(internal var ins: InputStream) : Runnable {
        override fun run() {
            while (true) {
                try {
                    listenForMovementForce()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InputMismatchException) {
                    e.printStackTrace()
                } catch (e: NoSuchElementException) {
                    e.printStackTrace()
                }
            }

        }

        fun listenForMovementForce() {
            waitForSemicolon(ins)

            val scanner = Scanner(ins)

            // Used for better precision
            var decimalX = 0f
            var decimalY = 0f

            val robot = Robot()

            while (true) {
                //print(scanner.nextInt()); print(" "); println(scanner.nextInt())

                var x =  scanner.nextFloat()                               * SENSITIVITY_FORCE + decimalX
                var y = (scanner.nextFloat() + GRAVITATIONAL_ACCELERATION) * SENSITIVITY_FORCE + decimalY

                decimalX = x % 1
                decimalY = y % 1

                x = Math.floor(x.toDouble()).toFloat()
                y = Math.floor(y.toDouble()).toFloat()

                println("x " + x)
                println("y " + y)

                robot.mouseMove(MouseInfo.getPointerInfo().location.x + x.toInt(),
                        MouseInfo.getPointerInfo().location.y + y.toInt())
            }
        }

        fun listenForMovementEuler() {
            waitForSemicolon(ins)

            val scanner = Scanner(ins)

            // Used for better precision
            var decimalX = 0f
            var decimalY = 0f

            val robot = Robot()

            while (true) {
                //print(scanner.nextInt()); print(" "); println(scanner.nextInt())

                var x =  scanner.nextFloat()   * SENSITIVITY_EULER + decimalX
                var y = (scanner.nextFloat() ) * SENSITIVITY_EULER + decimalY

                decimalX = x % 1
                decimalY = y % 1

                x = Math.floor(x.toDouble()).toFloat()
                y = Math.floor(y.toDouble()).toFloat()

                println("x " + x)
                println("y " + y)

                robot.mouseMove(MouseInfo.getPointerInfo().location.x + x.toInt(),
                        MouseInfo.getPointerInfo().location.y + y.toInt())
            }
        }

        fun waitForSemicolon(ins: InputStream) {
            var b = ins.read()
            while (b != '\n'.toInt()) { //ASCII semicolon
                println(b)
                b = ins.read()

                if(b == -1) {
                    //throw IOException()
                }
            }

        }
    }

}