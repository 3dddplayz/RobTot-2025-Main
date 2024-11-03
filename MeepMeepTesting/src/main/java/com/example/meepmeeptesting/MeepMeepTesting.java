package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.MecanumKinematics;
import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
//import com.acmerobotics.roadrunner.ftc.Actions;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-24, 60, -Math.PI/2))
                .strafeTo(new Vector2d(-8,24+8))
                .strafeTo(new Vector2d(-8,24+15))
                .strafeTo(new Vector2d(-36,24+15))
                .strafeTo(new Vector2d(-36,5))
                        .strafeTo(new Vector2d(-46,5))
                        .strafeTo(new Vector2d(-46,52))
                        .strafeTo(new Vector2d(-46,5))
                        .strafeTo(new Vector2d(-55,5))
                        .strafeTo(new Vector2d(-55,52))
                .strafeTo(new Vector2d(-55,5))
                .strafeTo(new Vector2d(-60,5))
                .strafeTo(new Vector2d(-60,52))


                .build());
        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}