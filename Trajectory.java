class Trajectory {
    double field;
    public Trajectory(double field){
        this.field=field;
    }

 public double getXPrime(double dRadius, double momentum){
     return Math.pow(dRadius,2)/(2*getRadius(momentum));
 }

private double getRadius(double momentum){ //getRadius=d
    return momentum/(0.3*field);

}
public double getYPrime(double dRadius, double momentum ){
    return Math.sqrt(Math.pow(dRadius,2)-(Math.pow(getXPrime(dRadius, momentum),2)));
}

public double getX(double momentum, double theta, double dRadius){

    return getXPrime(dRadius, momentum)*Math.cos(theta) - getYPrime(dRadius, momentum)*Math.sin(theta);


}

public double getY(double momentum, double theta, double dRadius){

    return getYPrime(dRadius, momentum)*Math.cos(theta) - getXPrime(dRadius, momentum)*Math.sin(theta);


}

public double getAngle(double momentum, double theta0, double dRadius){
    double theta = Math.PI/2 - theta0;
    return Math.atan2(getY(momentum, theta, dRadius), getX(momentum, theta, dRadius));
}

}
