package org.firstinspires.ftc.teamcode.sections;
// RR-specific imports
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

// Non-RR imports
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
public class Lifters {
    public DcMotor vertLifterR, vertLifterL;
    Servo  horLifterR, horLifterL;
    double horLiftPos = 0;
    public static class Params {
        public int lifterLimitHigh = 4350;
        public int lifterLimitLow = 0;
        public double lifterCorCoef = .0008;
        public double horPowerCoeff = .0025;
    }
    Params PARAMS = new Params();
    public Lifters(HardwareMap hardwareMap) {
        vertLifterR = hardwareMap.get(DcMotor.class, "lifterR");
        vertLifterR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vertLifterR.setDirection(DcMotor.Direction.REVERSE);
        vertLifterR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertLifterR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        vertLifterL = hardwareMap.get(DcMotor.class, "lifterL");
        vertLifterL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vertLifterL.setDirection(DcMotor.Direction.FORWARD);
        vertLifterL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertLifterL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        horLifterR = hardwareMap.get(Servo.class,"horLifterR");
        horLifterL = hardwareMap.get(Servo.class,"horLifterL");
        horLifterR.setDirection(Servo.Direction.REVERSE);
        horLifterL.setDirection(Servo.Direction.FORWARD);
    }

    public class SetVertLifterPos implements Action {
        double rPos,lPos,lifterAvgPos;
        int pos = 0;
        double power = 0;

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            rPos = vertLifterR.getCurrentPosition();
            lPos = vertLifterL.getCurrentPosition();
            lifterAvgPos = (rPos + lPos) / 2;

            vertLifterR.setPower(power + ((lifterAvgPos - rPos) * PARAMS.lifterCorCoef));
            vertLifterL.setPower(power + ((lifterAvgPos - lPos) * PARAMS.lifterCorCoef));


            return Math.abs(pos-lifterAvgPos) > 15;
            //2500
            //415

        }
    }
    //stupid setup sh*t
    public Action setVertLifterPos(int pos, double power) {
        SetVertLifterPos action = new SetVertLifterPos();
        action.pos = Math.min(Math.max(pos, PARAMS.lifterLimitLow), PARAMS.lifterLimitHigh);
        action.power = power;
        vertLifterR.setTargetPosition(pos);
        vertLifterL.setTargetPosition(pos);
        vertLifterR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vertLifterL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        return action;
    }

    public void setVertLifterPower(double pow){
        double lifterRpower = pow;
        double lifterLpower = pow;
        double rPos = vertLifterR.getCurrentPosition();
        double lPos = vertLifterL.getCurrentPosition();
        double lifterAvgPos = Math.max(Math.min((rPos+lPos)/2,PARAMS.lifterLimitHigh),PARAMS.lifterLimitLow);

        lifterRpower += (lifterAvgPos-rPos)*PARAMS.lifterCorCoef;
        lifterLpower += (lifterAvgPos-lPos)*PARAMS.lifterCorCoef;


        if(PARAMS.lifterLimitHigh>lifterAvgPos && pow>=0){
            vertLifterR.setPower(lifterRpower);
            vertLifterL.setPower(lifterLpower);
        }else if(PARAMS.lifterLimitLow<lifterAvgPos && pow<=0) {
            vertLifterR.setPower(lifterRpower);
            vertLifterL.setPower(lifterLpower);
        }else{
            vertLifterR.setPower(lifterRpower-pow);
            vertLifterL.setPower(lifterLpower-pow);
        }
    }

    public void setHorLifterPower(double power){
        horLiftPos = (horLifterL.getPosition() + horLifterR.getPosition())/2;
        horLifterR.setPosition(horLiftPos+power*PARAMS.horPowerCoeff);
        horLifterL.setPosition(horLiftPos+power*PARAMS.horPowerCoeff);
    }

    public class SetHorLifterPos implements Action {
        int pos = 0;
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            horLifterR.setPosition(pos);
            horLifterL.setPosition(pos);
            return false;
        }
    }
    //stupid setup sh*t
    public Action setHorLiftPos(int pos) {
        SetHorLifterPos action = new SetHorLifterPos();
        action.pos = Math.min(Math.max(pos, 0), 1);
        return action;
    }

}

