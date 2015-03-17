import java.io.*;
import java.util.Random;
public class MuonFactory{

  static Random value = new Random();
  static int underflow;
  double age = 0;
  Point position = 0;
  double mass =106;
  double minP ;
  double maxP ;
  double nextP ;

  public MuonFactory(double minP, double maxP)  {
      this.minP = minP;
      this.maxP = maxP;
  }

    /*random angle in radians generated and returned*/
    public double muonAngle()  {
        double nextangle;
        nextangle = value.nextDouble()*Math.PI*2;
        return nextangle;
    }

    /*Random Momentum in MeV/c generated and returned*/
    public double muonMomentum() {
        nextP =value.nextDouble()*(MaxP-MinP)+MinP;
        return nextP;
        }

    //double[] = {theta, momentum}
    public double[] newmuon()  {
        double [2] muon ={muonAngle() ,muonMomentum() };
        return muon;
    }
  }
