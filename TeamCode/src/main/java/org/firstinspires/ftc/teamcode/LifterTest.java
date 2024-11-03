package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@Autonomous(name = "Lifter Test", group = "Auto Testing")
public final class LifterTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(60, 60, Math.PI/4);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        Actions.runBlocking(
                new SequentialAction(
                        lift.setVertLifterPos(500,.5),
                        lift.setVertLifterPos(0,.5)
                )
        );
    }

}
