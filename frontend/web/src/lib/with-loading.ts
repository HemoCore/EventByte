import { FC } from "react";

export function withLoading<P>(
    Component: FC<P>,
    LoadingComponent: FC
) {
    const Extended = Component as FC<P> & { Loading: FC };
    Extended.Loading = LoadingComponent;
    return Extended;
}
