/*
Copyright 2018 Tianyi Zhang

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


    2020.3.15 - Replaced Kryonet by HMS Nearby Service.
    2020.3.15 - Add Chinese version.
    2020.3.15 - Add Game speed selection.
                Huawei Technologies Co.,Ltd. <wangmingqi@huawei.com>
 */

package com.huawei.nearby.game.snake.elements;

import static com.huawei.nearby.game.snake.helpers.Constants.HEIGHT;
import static com.huawei.nearby.game.snake.helpers.Constants.WIDTH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import com.huawei.nearby.game.snake.helpers.Constants;
import com.huawei.nearby.game.snake.helpers.Utils;

public class Foods {
    private final SortedSet<Integer> locations;

    private final Random random;

    public Foods() {
        locations = new TreeSet<Integer>();
        random = new Random(Constants.SEED);
    }

    public Foods(Foods other) {
        locations = new TreeSet<Integer>(other.locations);
        random = new Random(Constants.SEED);
    }

    public int getQuantity() {
        return locations.size();
    }

    /**
     *
     * @param exclude an ascending sorted array of Integers to be excluded from the random generation
     * @return
     */
    public Integer generateHelper(List<Integer> exclude) {
        Integer randomInt = Integer.valueOf(random.nextInt(HEIGHT * WIDTH - exclude.size()));
        for (Integer num : exclude) {
            if (randomInt.compareTo(num) < 0) {
                break;
            }
            randomInt += 1;
        }
        return randomInt;
    }

    public void generate(List<Snake> snakes) {
        List<Integer> exclude = new LinkedList<Integer>();
        for (int i = 0; i < Constants.WIDTH; ++i) {
            exclude.add(Utils.positionFromXy(i, 0));
            exclude.add(Utils.positionFromXy(i, Constants.HEIGHT - 1));
        }
        for (int i = 1; i < Constants.HEIGHT - 1; ++i) {
            exclude.add(Utils.positionFromXy(0, i));
            exclude.add(Utils.positionFromXy(Constants.WIDTH - 1, i));
        }
        for (Snake snake : snakes) {
            List<Integer> coords = snake.getCoordinates();
            for (int i = 0; i < coords.size(); i += 2) {
                exclude.add(Utils.positionFromXy(coords.get(i), coords.get(i + 1)));
            }
        }
        exclude.addAll(locations);
        Collections.sort(exclude);
        for (int i = 1; i <= Constants.MAX_FOOD_QUANTITY - getQuantity(); ++i) {
            Integer newLoc = generateHelper(exclude);
            locations.add(newLoc);
            int size = exclude.size();
            for (int j = 0; j <= size; ++j) {
                if (j == size) {
                    exclude.add(newLoc);
                } else if (newLoc < exclude.get(j)) {
                    exclude.add(j, newLoc);
                    break;
                }
            }
        }
    }

    public void consumedBy(Snake snake) {
        Integer headIndex = Utils.positionFromXy(snake.getHeadX(), snake.getHeadY());
        if (locations.contains(headIndex)) {
            locations.remove(headIndex);
            snake.grow();
        }
    }

    public boolean shouldGenerate() {
        return getQuantity() <= Constants.MIN_FOOD_QUANTITY;
    }

    public List<Integer> getLinearPositions() {
        List<Integer> positions = new ArrayList<Integer>(locations);
        return Collections.unmodifiableList(positions);
    }

    public void setLocations(List<Integer> linearPositions) {
        locations.clear();
        locations.addAll(linearPositions);
    }
}
