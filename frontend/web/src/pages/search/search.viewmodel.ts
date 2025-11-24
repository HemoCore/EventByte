import { AppDispatch } from "@/core/create-store";
import { SearchLocation, selectIsSearchLoading, selectSearchLocation, selectSearchRadius, selectSearchResults, setSearchLocation, setSearchRadius } from "@/core/search/search.slice";
import { searchEvents } from "@/core/search/usecases/search-events/search-events.usecase";
import { createSelector } from "@reduxjs/toolkit";

type BaseState = {
    location: SearchLocation
    radius: number
    setLocation: (location: SearchLocation) => void
    setRadius: (radius: number) => void
}
type LoadingState = BaseState & {
    type: "loading";
};

type EmptyState = BaseState & {
    type: "empty";
};

type WithEventsState = BaseState & {
    type: "with-events";
    events: string[];
};

export type SearchViewmodel = LoadingState | EmptyState | WithEventsState;

export const selectSearchViewmodel = (dispatch: AppDispatch) => createSelector(
    [selectIsSearchLoading, selectSearchResults, selectSearchLocation, selectSearchRadius],
    (isLoading, results, location, radius): SearchViewmodel => {
        const setRadiusAction = (radius: number) => {
            dispatch(setSearchRadius(radius))
            dispatch(searchEvents())
        }

        const setLocationAction = (location: SearchLocation) => {
            dispatch(setSearchLocation(location))
            dispatch(searchEvents())
        }


        if (isLoading) {
            return {
                type: "loading",
                location,
                radius,
                setRadius: setRadiusAction,
                setLocation: setLocationAction,
            };
        }

        if (results.length === 0) {
            return {
                type: "empty",
                location,
                radius,
                setRadius: setRadiusAction,
                setLocation: setLocationAction,
            };
        }

        return {
            type: "with-events",
            events: results,
            location,
            radius,
            setRadius: setRadiusAction,
            setLocation: setLocationAction,
        };
    }
);
