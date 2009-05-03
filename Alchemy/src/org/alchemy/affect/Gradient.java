/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2009 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.alchemy.affect;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.alchemy.core.*;

/**
 *
 * Gradient.java
 */
public class Gradient extends AlcModule {

//    private Color transparent;

    @Override
    protected void affect() {

        //System.out.println(canvas.createShapes.size() + " " + canvas.affectShapes.size());

        // int shapeTally = shapeCount * numOfCreateShapes;
//        Color bgColour = canvas.getBackgroundColour();
//        transparent = new Color(bgColour.getRed(), bgColour.getGreen(), bgColour.getBlue(), 0);

        for (int i = 0; i < canvas.createShapes.size(); i++) {
            setGradient(canvas.createShapes.get(i));
        }
        for (int i = 0; i < canvas.affectShapes.size(); i++) {
            setGradient(canvas.affectShapes.get(i));
        }
        
     
    }

    private void setGradient(AlcShape shape) {

        GeneralPath path = shape.getPath();

        Point2D p1 = null;
        Point2D p2 = null;

        // If the shape has been created with pen strokes
        if (shape.isPenShape()) {
            if (shape.hasSpine()) {
                ArrayList<Point2D.Float> spine = shape.getSpine();
                if (spine.size() > 0) {
                    p1 = spine.get(0);
                    p2 = spine.get(spine.size() / 2);
                }

            } else {
                PathIterator iterator = path.getPathIterator(null);
                float[] currentPoints = new float[6];
                search:
                while (!iterator.isDone()) {
                    switch (iterator.currentSegment(currentPoints)) {
                        case PathIterator.SEG_MOVETO:
                            // Use the start point for the first point
                            p1 = new Point2D.Float(currentPoints[0], currentPoints[1]);
                            break search;

                    }
                    iterator.next();
                }
                // Use the last point for the second point
                p2 = path.getCurrentPoint();
            }


        // Else if the shape has been not been created with pen strokes
        // Then lets make a random gradient
        } else {
            if (shape.getGradientPaint() == null) {
                Rectangle bounds = path.getBounds();
                p1 = getRandomPoint(bounds);
                p2 = getRandomPoint(bounds);
            }
        }

        if (p1 != null && p2 != null) {
            Color colour = shape.getColour();
            Color transparent = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 0);
            GradientPaint gradient = new GradientPaint(p1, shape.getColour(), p2, transparent);
            //System.out.println("1: " + shape.getColour().getTransparency() + " 2: " + transparent.getTransparency());
            shape.setGradientPaint(gradient);
        }
    }

    private Point2D.Float getRandomPoint(Rectangle bounds) {
        float x = math.random(bounds.x, bounds.x + bounds.width);
        float y = math.random(bounds.y, bounds.y + bounds.height);
        return new Point2D.Float(x, y);
    }
}
