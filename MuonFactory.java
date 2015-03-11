import java.io.*;
import java.util.Random;
class MuonFactory{

  static BufferedReader keyboard = new BufferedReader (new InputStreamReader(System.in)) ;
  static PrintWriter screen = new PrintWriter( System.out, true);
  static Random value = new Random();
  static int underflow;
  static double age = 0
  static Point position = 0
  static double mass =106

  Config config = new Config("Muon");

  public class MuonFactory(){
  return muon;
  }

    /*random angle in radians generated and returned*/

    public static double muonAngle()
    {
        double nextangle;
        nextangle = value.nextDouble()*Math.PI*2;
        return nextangle;
    }
    /*Random Momentum in MeV/c generated and returned*/

    public static double muonMomentum(){
        double nextP;
        double MaxP = config.getDouble("MaxP");
        double MinP = config.getDouble("MinP");
        nextP =value.nextDouble()*(MaxP-MinP)+MinP;
        return nextP;
        }

    static Particle muon = new particle(mass, nextP, position, nextangle, age);
    return muon;

  }
