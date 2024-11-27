package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Intake;
import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@TeleOp(name = "Lifter Fix")
public final class LifterFix extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(60, 60, Math.PI/4);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        while(opModeIsActive()){
            if(gamepad2.a){
                lift.vertLifterR.setPower(-.2);
                lift.vertLifterL.setPower(-.2);
            }else{
                lift.vertLifterR.setPower(-.1);
                lift.vertLifterL.setPower(-.1);
            }
        }
    }

}
