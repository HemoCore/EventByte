import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../create-store";
import { searchEvents } from "./usecases/search-events/search-events.usecase";

export type SearchLocation = {
    name: string;
    lat: number;
    lng: number;
};

type SearchState = {
    location: SearchLocation;
    radius: number;
    query: string;
    results: string[];
    loading: boolean;
};

const initialState: SearchState = {
    location: {
        name: "Paris",
        lat: 48.8585,
        lng: 2.3475
    },
    radius: 10_000,
    query: "",
    results: [],
    loading: false,
};

export const searchSlice = createSlice({
    name: "search",
    initialState,
    reducers: {
        setSearchLocation: (state, action: PayloadAction<SearchLocation>) => {
            state.location = action.payload;
        },
        setSearchRadius: (state, action: PayloadAction<number>) => {
            state.radius = action.payload;
        },
        setSearchQuery: (state, action: PayloadAction<string>) => {
            state.query = action.payload;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(searchEvents.pending, (state) => {
                state.loading = true;
            })
            .addCase(searchEvents.fulfilled, (state, action) => {
                state.loading = false;
                state.results = action.payload.map((event) => event.id);
            })
            .addCase(searchEvents.rejected, (state) => {
                state.loading = false;
            });
    },
});

export const { setSearchLocation, setSearchRadius, setSearchQuery } =
    searchSlice.actions;

export const selectSearchLocation = (state: RootState) => state.search.location;
export const selectSearchRadius = (state: RootState) => state.search.radius;
export const selectSearchQuery = (state: RootState) => state.search.query;
export const selectSearchResults = (state: RootState) => state.search.results;
export const selectIsSearchLoading = (state: RootState) => state.search.loading;
