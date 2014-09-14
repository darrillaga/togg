package com.t3coode.togg.activities.utils;

import com.t3coode.togg.services.dtos.ProjectDTO;

public class ProjectColor {

    public static int getColor(ProjectDTO project, int[] colors) {
        int projectColor = Integer.parseInt(project.getColor()) % 14;

        return colors[(colors.length > projectColor) ? projectColor
                : colors.length - 1];
    }
}
