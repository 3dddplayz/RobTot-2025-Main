package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
//import com.acmerobotics.roadrunner.ftc.Actions;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(0, 0, 0))
                .splineTo(new Vector2d(20, 20), Math.PI / 2)
                .splineTo(new Vector2d(0, 40), Math.PI)
                .splineTo(new Vector2d(-20, 20), 3*Math.PI/2)
                .splineTo(new Vector2d(0, 0), 0)
                .splineTo(new Vector2d(20, 20), Math.PI / 2)
                .splineTo(new Vector2d(0, 40), Math.PI)
                .splineTo(new Vector2d(-20, 20), 3*Math.PI/2)
                .splineTo(new Vector2d(0, 0), 0)
                .strafeToLinearHeading(new Vector2d(20, 0),-Math.PI / 2)
                .strafeToLinearHeading(new Vector2d(20, 40),-Math.PI )
                .strafeToLinearHeading(new Vector2d(-20, 40),-3*Math.PI / 2)
                .strafeToLinearHeading(new Vector2d(-20, 0),0)
                .strafeToLinearHeading(new Vector2d(0, 0),0)

                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}