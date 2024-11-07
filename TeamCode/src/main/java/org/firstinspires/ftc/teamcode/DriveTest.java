package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Lifters;

import org.firstinspires.ftc.teamcode.sections.MecanumDrive;

@Autonomous(name = "Drive Test", group = "Auto Testing")
public final class DriveTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(-40, -40, Math.PI/4);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        Actions.runBlocking(
            new SequentialAction(
                new ParallelAction(
                    drive.actionBuilder(beginPose)
                            .setTangent(3*Math.PI/4)
                            .splineToLinearHeading(new Pose2d(-45,0,0),Math.PI/2)
                            .splineToLinearHeading(new Pose2d(-40,40,-Math.PI/4),Math.PI/4)

                            .splineToLinearHeading(new Pose2d(0,45,-Math.PI/2),0)
                            .splineToLinearHeading(new Pose2d(40,40,-3*Math.PI/4),-Math.PI/4)

                            .splineToLinearHeading(new Pose2d(45,0,Math.PI),-Math.PI/2)
                            .splineToLinearHeading(new Pose2d(40,-40,3*Math.PI/4),-3*Math.PI/4)

                            .splineToLinearHeading(new Pose2d(0,-45,Math.PI/2),Math.PI)
                            .splineToLinearHeading(new Pose2d(-40,-40,Math.PI/4),3*Math.PI/4)
                        .build()
                ),
                new InstantAction(() -> lift.setVertLifterPower(0))
            )
        );
    }
}
