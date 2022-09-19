import lejos.hardware.sensor.EV3ColorSensor; // importações
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import lejos.hardware.Sound;


public class Resgate2022 {


        static EV3ColorSensor sensorCorEsq = new EV3ColorSensor(SensorPort.S4); // criação e declaração do objeto sensorCorEsq indicando porta
        static SampleProvider provedorCorEsq = sensorCorEsq.getRGBMode(); // responsável pelo método que mede os valores RGB
        static float[] amostraCorEsq = new float[provedorCorEsq.sampleSize()]; // armazenará os valores RGB


        static EV3ColorSensor sensorCorDir = new EV3ColorSensor(SensorPort.S1);
        static SampleProvider provedorCorDir = sensorCorDir.getRGBMode();
        static float[] amostraCorDir = new float[provedorCorDir.sampleSize()];


        static EV3UltrasonicSensor sensorUS = new EV3UltrasonicSensor(SensorPort.S2); // tomar cuidado com a porta
        static SampleProvider provedorUS = sensorUS.getDistanceMode(); // sensor ultrassônico
        static float[] amostraUS = new float[provedorUS.sampleSize()];


        static UnregulatedMotor motorEsq = new UnregulatedMotor(MotorPort.D); // o método principal só aceita objetos estáticos
        static UnregulatedMotor motorDir = new UnregulatedMotor(MotorPort.B);
        
        public static void main(String[] args) {                 // ------------------------------------------------------------------
                
                boolean verdeEsq = false;
                boolean verdeDir = false;
                boolean permitir = false;
                
                LCD.drawString("sensorEsq", 0, 3);
                Button.waitForAnyPress(); // congelar o programa até que qualquer botão seja pressionado
                LCD.drawString("         ", 0, 3);
                provedorCorEsq.fetchSample(amostraCorEsq, 0);
                float corteEsq = amostraCorEsq[0];
                LCD.drawString("CorteEsq:    "+ corteEsq, 0, 2);
                Delay.msDelay(100);
                
                LCD.drawString("sensorDir", 0, 3);
                Button.waitForAnyPress(); // congelar o programa até que qualquer botão seja pressionado
                LCD.drawString("         ", 0, 3);
                provedorCorDir.fetchSample(amostraCorDir, 0);
                float corteDir = amostraCorDir[0];
                LCD.drawString("CorteDir:    "+ corteDir, 0, 7);
                Delay.msDelay(100);
                
                LCD.drawString("aperte ENTER", 0, 3);
                Button.waitForAnyPress(); // congelar o programa até que qualquer botão seja pressionado
                LCD.drawString("            ", 0, 3);


                // ---------------------------------------------------------------------------------------------------------------
                
                while (Button.ESCAPE.isUp()) { // enquanto botão escape não for apertado
                        
                        if(Button.DOWN.isDown()) {                // pause
                                movimento(0,0,0);
                                Delay.msDelay(500);
                                Button.waitForAnyPress();
                        }
                        
                        provedorCorEsq.fetchSample(amostraCorEsq, 0); // mede em RGB
                        LCD.drawString("R_Esq        " + amostraCorEsq[0], 0, 0); // e exibe na tela
                        LCD.drawString("G_Esq        " + amostraCorEsq[1], 0, 1);
                        
                        provedorCorDir.fetchSample(amostraCorDir, 0);
                        LCD.drawString("R_Dir        " + amostraCorDir[0], 0, 5);
                        LCD.drawString("G_Dir        " + amostraCorDir[1], 0, 6);
                        
                        provedorUS.fetchSample(amostraUS, 0);
                        
                        // ---------------------------------------------------------------------------------------------------------------
                        
                        // conferir verde
                        if(amostraCorEsq[1] > amostraCorEsq[0] * 2) {
                                verdeEsq = true;
                        } else {
                                verdeEsq = false;
                        }
                                                
                        if(amostraCorDir[1] > amostraCorDir[0] * 2) {
                                verdeDir = true;
                        } else {
                                verdeDir = false;
                        }
                                                
                        if(permitir) {
                                if (verdeEsq && verdeDir) {
                                        Sound.beep();
                                        movimento(-50, 50, 2000); // 180°
                                        movimento(50,50,400);
                                        
                                } else if (verdeEsq) {
                                        Sound.beep();
                                        movimento(-20, 50, 800); // vira para esquerda
                                        
                                } else if (verdeDir) {
                                        Sound.beep();
                                        movimento(50, -20, 800); // vira para direita
                                }
                        }
                                
                        // ---------------------------------------------------------------------------------------------------------------
                        
                        if(amostraUS[0] < 0.05) {
                                
                                movimento(-30,-30,800);                            // atrás
                                movimento(60,-60,650);                             // mira
                                do {
                                        movimento(28,60,1);                            // arco
                                        provedorCorDir.fetchSample(amostraCorDir, 0);
                                } while(amostraCorDir[0] > corteDir);
                                movimento(60,-60,200);                             // correção
                        }
                        
                        // ---------------------------------------------------------------------------------------------------------------
                        
                        if (amostraCorEsq[0] > corteEsq) { // Esq: Branco


                                if (amostraCorDir[0] > corteDir) { // Dir: Branco
                                        //Branco e Branco
                                        movimento(50, 50, 1);
                                        LCD.drawString("branco     branco     ", 0, 3);
                                        
                                } else { // Dir: Preto
                                        //Branco e Preto
                                        movimento(50, -30, 1);
                                        LCD.drawString("branco     preto     ", 0, 3);
                                }
                                permitir = true;
                                
                        } else { // Esq: Preto
                                
                                if (amostraCorDir[0] > corteDir) { // Dir: Branco
                                        // Preto e Branco
                                        movimento(-30, 50, 1);
                                        LCD.drawString("preto     branco     ", 0, 3);
                                        permitir  = true;
                                        
                                } else { // Dir: Preto
                                        // Preto e Preto
                                        //movimento(30, 30, 1);
                                        movimento(20, 20, 1);
                                        LCD.drawString("preto     preto     ", 0, 3);
                                        permitir = false;
                                }
                        }
                }
                motorDir.close();
                motorEsq.close();
                sensorCorDir.close();
                sensorCorEsq.close();
                sensorUS.close();
        }
        
        
        // ---------------------------------------------------------------------------------------------------------------
        
        public static void movimento(int mE, int mD, int tempo) { // com parâmetro de tempo
                motorEsq.setPower(-mE);
                motorDir.setPower(-mD);
                Delay.msDelay(tempo);
        }
}