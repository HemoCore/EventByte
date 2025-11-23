import { Skeleton } from "@/design-system/components/ui/skeleton"

export const EventCardSkeleton = () => {
    return (
        <div className="group">
            <Skeleton className="aspect-[16/9] w-full rounded-lg mb-4" />

            <div className="space-y-2">
                <Skeleton className="h-6 w-3/4" />
                <Skeleton className="h-6 w-1/2" />

                <Skeleton className="h-4 w-2/3" />

                <div className="flex items-center justify-between pt-2">
                    <Skeleton className="h-4 w-1/3" />
                    <Skeleton className="h-6 w-16" />
                </div>
            </div>
        </div>
    )
}
