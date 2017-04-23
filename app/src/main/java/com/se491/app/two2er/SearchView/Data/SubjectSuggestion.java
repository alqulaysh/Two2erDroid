package com.se491.app.two2er.SearchView.Data;

/**
 * Created by eoliv on 4/23/2017.
 */

import android.os.Parcel;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

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

public class SubjectSuggestion implements SearchSuggestion {

    private String mColorName;
    private boolean mIsHistory = false;

    public SubjectSuggestion(String suggestion) {
        this.mColorName = suggestion.toLowerCase();
    }

    public SubjectSuggestion(Parcel source) {
        this.mColorName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mColorName;
    }

    public static final Creator<SubjectSuggestion> CREATOR = new Creator<SubjectSuggestion>() {
        @Override
        public SubjectSuggestion createFromParcel(Parcel in) {
            return new SubjectSuggestion(in);
        }

        @Override
        public SubjectSuggestion[] newArray(int size) {
            return new SubjectSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mColorName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}