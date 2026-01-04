import {useEffect, useState} from "react";

export default function useIsClient() {
    const [isClient, setIsClient] = useState(false)
    useEffect(() => {
        console.log("isClient: ", isClient)
        setIsClient(true)
    }, [])
    return isClient
}
