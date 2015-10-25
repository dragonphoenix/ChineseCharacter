/*
 *  Copyright (C) 2012 Dragon
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dragon.lib.utils;

import android.content.res.Resources;

import com.dragon.lib.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/25.
 */
final public class CharacterUtil {
    private static CharacterUtil mInstance = null;
    private Map<String, List<String>> mCharacters = null;

    private CharacterUtil() {
        mCharacters = new HashMap<String, List<String>>();
    }

    public static CharacterUtil getInstance() {
        if (mInstance == null) {
            synchronized (CharacterUtil.class) {
                if (mInstance == null) {
                    mInstance = new CharacterUtil();
                }
            }
        }

        return mInstance;
    }

    private String readCharactersFile(Resources resources) {
        try {
            InputStream in = resources.openRawResource(R.raw.chinese_characters);
            int length = in.available();
            byte [] buffer = new byte[length];
            in.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (Exception e) {

        }

        return "";
    }

    private void parseCharacters(Resources resources){
        String [] elems;
        List<String> pinyinList;
        String content = readCharactersFile(resources);
        String character;

        String [] lines = content.split("\r\n");
        for (int i = 0; i < lines.length; i++){
            elems = lines[i].split(" ");
            pinyinList = new ArrayList<String>(Arrays.asList(elems));
            character = pinyinList.remove(0);
            mCharacters.put(character, pinyinList);
        }
    }

    public Map<String, List<String>> getCharacters(Resources resources){
        if (mCharacters.size() <= 0){
            parseCharacters(resources);
        }

        return mCharacters;
    }
}
