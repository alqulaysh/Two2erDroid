package com.se491.app.two2er.SearchView.Data;

/**
 * Created by eoliv on 4/23/2017.
 */

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.content.Context;
        import android.widget.Filter;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import java.io.IOException;
        import java.io.InputStream;
        import java.lang.reflect.Type;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;

public class DataHelper {

    private static final String COLORS_FILE_NAME = "colors.json";

    private static List<SubjectWrapper> sSubjectWrappers = new ArrayList<>();

    private static List<SubjectSuggestion> sSubjectSuggestions =
            new ArrayList<>(Arrays.asList(
                    new SubjectSuggestion("Science"),
                    new SubjectSuggestion("Math"),
                    new SubjectSuggestion("Music"),
                    new SubjectSuggestion("Biology"),
                    new SubjectSuggestion("Physics"),
                    new SubjectSuggestion("Chemistry"),
                    new SubjectSuggestion("Computer Science")));

    public interface OnFindColorsListener {
        void onResults(List<SubjectWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<SubjectSuggestion> results);
    }

    public static List<SubjectSuggestion> getHistory(Context context, int count) {

        List<SubjectSuggestion> suggestionList = new ArrayList<>();
        SubjectSuggestion subjectSuggestion;
        for (int i = 0; i < sSubjectSuggestions.size(); i++) {
            subjectSuggestion = sSubjectSuggestions.get(i);
            subjectSuggestion.setIsHistory(true);
            suggestionList.add(subjectSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (SubjectSuggestion subjectSuggestion : sSubjectSuggestions) {
            subjectSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<SubjectSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (SubjectSuggestion suggestion : sSubjectSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<SubjectSuggestion>() {
                    @Override
                    public int compare(SubjectSuggestion lhs, SubjectSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<SubjectSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findColors(Context context, String query, final OnFindColorsListener listener) {
        initColorWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                List<SubjectWrapper> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (SubjectWrapper color : sSubjectWrappers) {
                        if (color.getName().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(color);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<SubjectWrapper>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {

        if (sSubjectWrappers.isEmpty()) {
            String jsonString = loadJson(context);
            sSubjectWrappers = deserializeColors(jsonString);
        }
    }

    private static String loadJson(Context context) {

        String jsonString;

        try {
            InputStream is = context.getAssets().open(COLORS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;
    }

    private static List<SubjectWrapper> deserializeColors(String jsonString) {

        Gson gson = new Gson();

        Type collectionType = new TypeToken<List<SubjectWrapper>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }

}