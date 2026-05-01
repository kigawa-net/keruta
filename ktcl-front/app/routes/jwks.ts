import {Auth} from "../auth";

export async function loader() {


    return {
        keys: [
            await Auth.getJwks()
        ],
    };
}
