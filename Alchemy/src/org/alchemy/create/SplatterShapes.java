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
package org.alchemy.create;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.alchemy.core.*;

/**
 *
 * SplatterShapes.java
 */
public class SplatterShapes extends AlcModule {

    private Point2D.Float start;
    private Point2D.Float end;
    private float newSizeInfluence,  midPointPush,  maxLineWidth,  size;

    @Override
    protected void setup() {
        newSizeInfluence = 0.5F;
        midPointPush = 0.75F;
        //newSizeInfluence = (float) Math.floor(math.random(20) / 10F) - 0.5F;
        //midPointPush = (float) (Math.floor(math.random(8)) / 4F) - 1F;
        maxLineWidth = math.random(50) + 50F;
        size = 1;
    }

    private void splat(Point2D.Float start, Point2D.Float end, Point2D.Float mid, float d) {

        AlcShape firstShape = new AlcShape(start);
        if (d < 0) {
            d = 0;
        }
        firstShape.setLineWidth(d);
        GeneralPath path = firstShape.getPath();
        path.quadTo(mid.x, mid.y, end.x, end.y);
        canvas.createShapes.add(firstShape);

        // splotch
        float splotch = (float) Math.sqrt(Math.pow((end.x - start.x), 2) + Math.pow((end.y - start.y), 2));

        for (int i = 0; i < Math.floor(5F * Math.pow(math.random(1), 4)); i++) {
            // positioning of splotch varies between �4dd, tending towards 0
            int splat_range = 1;
            float x4 = (float) (splotch * 1F * (Math.pow(math.random(1), splat_range) - (splat_range / 2F)));
            float y4 = (float) (splotch * 1F * (Math.pow(math.random(1), splat_range) - (splat_range / 2F)));
            // direction of splotch varies between �0.5
            float x5 = math.random(1) - 0.5F;
            float y5 = math.random(1) - 0.5F;
            float dd = d * (0.5F + math.random(1));

            AlcShape shape = new AlcShape(new Point2D.Float(start.x + x4, start.y + y4));
            if (dd < 0) {
                dd = 0;
            }
            shape.setLineWidth(dd);
            shape.addCurvePoint(new Point2D.Float((start.x + x4 + x5), (start.y + y4 + y5)));
            canvas.createShapes.add(shape);
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = new Point2D.Float(e.getX(), e.getY());
        end = new Point2D.Float(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        Point2D.Float mid = new Point2D.Float(
                ((end.x - start.x) * (1F + midPointPush)) + start.x,
                ((end.y - start.y) * (1F + midPointPush)) + start.y);

        start = end;
        end = new Point2D.Float(e.getX(), e.getY());

        float distance = (float) Math.sqrt(Math.pow((end.x - start.x), 2) + Math.pow((end.y - start.y), 2));
        // Avoid INFINITY
        if (distance == 0) {
            distance = 1F;
        }
        float newSize = maxLineWidth / distance;
        size = (newSizeInfluence * newSize) + ((1F - newSizeInfluence) * size);

        splat(start, end, mid, size);

        canvas.redraw();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.commitShapes();
    }

    private static int getCursorSpeed(Point p1, Point p2) {
        int diffX = Math.abs(p1.x - p2.x);
        int diffY = Math.abs(p1.y - p2.y);
        return diffX + diffY;
    }
}
