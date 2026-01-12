export default function FormErrMsg(
    {
        err,
    }:
    {
        err?: string
    },
) {


    return (
        err && (
            <p className="mt-2 text-sm text-red-600">{err}</p>
        )
    )
}


