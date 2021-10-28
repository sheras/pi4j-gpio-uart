package com.pi4j.example;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: EXAMPLE  :: Sample Code
 * FILENAME      :  DigitalInputOutput.java
 *
 * This file is based on the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2021 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.util.Console;

import java.io.IOException;

/**
 * <p>This example fully describes the base usage of Pi4J by providing extensive comments in each step.</p>
 */
public class DigitalInputOutputGPIOmedidor {

    private static final int PIN_TRIGGER = 16; // BCM 16 OUTPUT
    private static final int PIN_ECHO = 20; // BCM 20 INPUT
    private static final int INTERVALO_ENTRE_LECTURAS = 3000;

    /**
     * This application uses HC-SR04 to measure distance.
     *
     * @throws java.lang.Exception if any.
     */
    public static double leerDistancia(DigitalOutput mTrigger, DigitalInput mEcho, Console console) throws InterruptedException {
        int hazAlgo;
        double distancia = 0.0;

        mTrigger.setState(false);
        Thread.sleep(0, 2000); // 2 mseg
        mTrigger.setState(true);
        Thread.sleep(0, 10000); //10 msec
        mTrigger.setState(false);
        while (mEcho.state().equals(false)) {
            hazAlgo = 0;
            //console.println("Echo state: " + false);
        }
        long tiempoIni = System.nanoTime();
        while (mEcho.state().equals(true)) {
            hazAlgo = 1;
            //console.println("Echo state: " + true);
        }

        //console.println("Echo state, again: " + false);
        long tiempoFin = System.nanoTime();
        long anchoPulso = tiempoFin - tiempoIni;
        distancia = (anchoPulso / 1000.0) / 58.23; //cm
        return distancia;
    }

    public static void main(String[] args) throws Exception {
        // Create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
        Console console = new Console();
        int numDistancias = 0;

        // Print program title/header
        console.title("<-- The Pi4J Project -->", "HC-SR04 Example project");

        // ************************************************************
        //
        // WELCOME TO Pi4J:
        //
        // Here we will use this getting started example to
        // demonstrate the basic fundamentals of the Pi4J library.
        //
        // This example is to introduce you to the boilerplate
        // logic and concepts required for all applications using
        // the Pi4J library.  This example will do use some basic I/O.
        // Check the pi4j-examples project to learn about all the I/O
        // functions of Pi4J.
        //
        // ************************************************************

        // ------------------------------------------------------------
        // Initialize the Pi4J Runtime Context
        // ------------------------------------------------------------
        // Before you can use Pi4J you must initialize a new runtime
        // context.
        //
        // The 'Pi4J' static class includes a few helper context
        // creators for the most common use cases.  The 'newAutoContext()'
        // method will automatically load all available Pi4J
        // extensions found in the application's classpath which
        // may include 'Platforms' and 'I/O Providers'
        Context pi4j = Pi4J.newAutoContext();

        // ------------------------------------------------------------
        // Output Pi4J Context information
        // ------------------------------------------------------------
        // The created Pi4J Context initializes platforms, providers
        // and the I/O registry. To help you to better understand this
        // approach, we print out the info of these. This can be removed
        // from your own application.
        // OPTIONAL
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);

        // Here we will create I/O interfaces for a (GPIO) digital output
        // and input pin. We define the 'provider' to use PiGpio to control
        // the GPIO.
        DigitalOutputConfigBuilder trigConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("trigger")
                .name("HC-SR04 Trigger")
                .address(PIN_TRIGGER)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        DigitalOutput mTrigger = pi4j.create(trigConfig);

        DigitalInputConfigBuilder echoConfig = com.pi4j.io.gpio.digital.DigitalInput.newConfigBuilder(pi4j)
                .id("echo")
                .name("HC-SR04 Echo")
                .address(PIN_ECHO)
                .debounce(3000L)
                .provider("pigpio-digital-input");
        DigitalInput mEcho = pi4j.create(echoConfig);

        while (numDistancias < 10){
            try {
                double distancia  = leerDistancia(mTrigger,mEcho,console);
                numDistancias++;
                console.println("LEYENDO Distancia " + numDistancias + ": " + distancia);
                Thread.sleep(INTERVALO_ENTRE_LECTURAS);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

        // OPTIONAL: print the registry
        PrintInfo.printRegistry(console, pi4j);

        // ------------------------------------------------------------
        // Terminate the Pi4J library
        // ------------------------------------------------------------
        // We we are all done and want to exit our application, we must
        // call the 'shutdown()' function on the Pi4J static helper class.
        // This will ensure that all I/O instances are properly shutdown,
        // released by the the system and shutdown in the appropriate
        // manner. Terminate will also ensure that any background
        // threads/processes are cleanly shutdown and any used memory
        // is returned to the system.

        // Shutdown Pi4J
        pi4j.shutdown();

    }
}
