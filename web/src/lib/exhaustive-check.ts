export const exhaustiveCheck = (x: never): never => {
    throw new Error(`Unexpected object: ${x}`);
}
