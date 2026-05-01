import {Route} from "../../.react-router/types/app/routes/+types";
import {Auth} from "../auth";

export async function action({request}: Route.ActionArgs) {
    const body: {
        token: string
    } = await request.json().catch(reason => console.error(reason))
    if (!body.token) {
        return {
            status: 400,
            body: {
                error: "token is required"
            }
        }
    }
    const res = await Auth.verifyUserJwt(body.token)

    return {
        token: await Auth.getJwt(res.payload.sub)
    }
}
