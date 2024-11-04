package org.firstinspires.ftc.teamcode.sections;

import android.drm.DrmStore;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {
    public static class Params {
        double intakeSpeed = 1;
    }
    Params PARAMS = new Params();
    CRServo intakeR, intakeL;
    Servo trunkR, trunkL, twist;
    public Intake(HardwareMap hardwareMap){
        intakeR = hardwareMap.get(CRServo.class,"intakeR");
        intakeL = hardwareMap.get(CRServo.class,"intakeL");
        intakeR.setDirection(CRServo.Direction.FORWARD);
        intakeL.setDirection(CRServo.Direction.REVERSE);

        trunkR = hardwareMap.get(Servo.class,"trunkR");
        trunkL = hardwareMap.get(Servo.class,"trunkL");
        twist = hardwareMap.get(Servo.class,"twist");
        trunkR.setDirection(Servo.Direction.FORWARD);
        trunkL.setDirection(Servo.Direction.REVERSE);
        twist.setDirection(Servo.Direction.REVERSE);
    }
    public void intakeIn(){
        intakeR.setPower(PARAMS.intakeSpeed);
        intakeL.setPower(PARAMS.intakeSpeed);
    }

    public void intakeOut(){
        intakeR.setPower(-PARAMS.intakeSpeed);
        intakeL.setPower(-PARAMS.intakeSpeed);
    }

    public void intakeOff(){
        intakeR.setPower(0);
        intakeL.setPower(0);
    }

    //Auto Actions
    public Action IntakeIn(){
        return new Action(){
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                intakeIn();
                return false;
            }
        };
    }
    public Action IntakeOut(){
        return new Action(){
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                intakeOut();
                return false;
            }
        };
    }
    public Action IntakeOff(){
        return new Action(){
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                intakeOff();
                return false;
            }
        };
    }

}
