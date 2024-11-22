package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Intake;
import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@Autonomous(name = "Trunk Test", group = "Auto Testing")
public final class TrunkTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(60, 60, Math.PI/4);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        Intake intk = new Intake(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        intk.setTrunkPos(0);
        while(!gamepad1.a&&opModeIsActive()){
            if(gamepad1.a){
                break;
            }
        }
        while(opModeIsActive()){
            intk.setTrunkPos(90);
        }
    }

}
