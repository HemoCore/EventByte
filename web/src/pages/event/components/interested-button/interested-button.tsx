import { RequireAuth } from "@/components/require-auth";
import { Button } from "@/design-system/components/ui/button";
import { useAppDispatch } from "@/hooks/store-hooks";
import { FC } from "react";
import { useSelector } from "react-redux";
import { Skeleton } from "@/design-system/components/ui/skeleton";
import { selectInterestedButtonViewModel } from "./interested-button.viewmodel";
import { Heart, HeartFilled } from "@/components/icons";

type Props = {
    eventId: string
}

export const InterestedButton: FC<Props> = ({ eventId }) => {
    const dispatch = useAppDispatch();
    const {
        isLoading,
        isLiked,
        toggleLike
    } = useSelector(selectInterestedButtonViewModel({ eventId, dispatch }));

    if (isLoading) {
        return <Skeleton className="h-1O w-34" />
    }

    return (
        <RequireAuth>
            <Button size="lg" variant="secondary" className="text-base uppercase" onClick={toggleLike}>
                {isLiked ? <HeartFilled className="h-4 w-4 mr-2 text-highlight fill-current" /> : <Heart className="h-4 w-4 mr-2" />}
                Interested
            </Button>
        </RequireAuth>
    )

}
