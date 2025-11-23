export type AuthUserEntity = {
    id: string;
    fullname: string;
    email: string
}

export const authUserEntityBuilder = (authUser: AuthUserEntity = {
    id: '1',
    fullname: 'John Doe',
    email: 'john@example.com'
}) => ({
    withId: (id: string) => authUserEntityBuilder({ ...authUser, id }),
    withEmail: (email: string) => authUserEntityBuilder({ ...authUser, email }),
    withFullname: (fullname: string) => authUserEntityBuilder({ ...authUser, fullname }),
    build: () => authUser,
})
