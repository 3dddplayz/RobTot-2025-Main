package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Intake;
import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@Autonomous(name = "HP Auto", group = "Auto Testing")
public final class HumanPlayerAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(24, -60, Math.PI/2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        Intake intk = new Intake(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        Actions.runBlocking(
            new SequentialAction(
                    intk.SetTrunkPos(90),
                    intk.SetTwistPos(90),
                    new ParallelAction(
                        drive.actionBuilder(beginPose)
                            .setTangent(3*Math.PI/4)
                            .splineToConstantHeading(new Vector2d(6,-24-12),3*Math.PI/4,new TranslationalVelConstraint(25))
                            .splineToConstantHeading(new Vector2d(3,-24-10),Math.PI)
                                .build()

                    ,new SequentialAction(
                        lift.setVertLifterPos(2520,1)

                    )

                ),
                    lift.setVertLifterPos(1600,.7),
                    new ParallelAction(
                            drive.actionBuilder(new Pose2d(8,-24-10,Math.PI/2))
                                    .setTangent(0)
                                    .splineToSplineHeading(new Pose2d(43,-24*3+17,3*Math.PI/2),0)
                                    .splineToConstantHeading(new Vector2d(47,-24*3+13),-Math.PI/2)

                                    //regrab 2
                                    .strafeToConstantHeading(new Vector2d(47,-24*3+15))
                                    .setTangent(4.5*Math.PI/6)
                                    .splineToSplineHeading(new Pose2d(8.5,-24-12,-3*Math.PI/2),Math.PI/2)
                                    .splineToConstantHeading(new Vector2d(5.5,-24-10),Math.PI)
                                    .build(),
                            new SequentialAction(
                                    lift.setVertLifterPos(700,.7),
                                    intk.IntakeIn(),
                                    new SleepAction(2),
                                    drive.waitForX(40),
                                    lift.setVertLifterPos(2520,.7),
                                    intk.IntakeOff()
                            )
                    ),
                    lift.setVertLifterPos(1600,.7),
                    new ParallelAction(
                            drive.actionBuilder(new Pose2d(8,-24-10,Math.PI/2))
                                    .setTangent(0)
                                    .splineToSplineHeading(new Pose2d(43,-24*3+17,3*Math.PI/2),0)
                                    .splineToConstantHeading(new Vector2d(47,-24*3+13),-Math.PI/2)

                                    //regrab 2
                                    .strafeToConstantHeading(new Vector2d(47,-24*3+15))
                                    .setTangent(4.5*Math.PI/6)
                                    .splineToSplineHeading(new Pose2d(13,-24-12,-3*Math.PI/2),Math.PI/2)
                                    .splineToConstantHeading(new Vector2d(10,-24-10),Math.PI)
                                    .build(),
                            new SequentialAction(
                                    lift.setVertLifterPos(700,.7),
                                    intk.IntakeIn(),
                                    new SleepAction(2),
                                    drive.waitForX(40),
                                    lift.setVertLifterPos(2520,.7),
                                    intk.IntakeOff()
                            )
                    ),
                    lift.setVertLifterPos(0,.7)

            )
        );
    }

}
