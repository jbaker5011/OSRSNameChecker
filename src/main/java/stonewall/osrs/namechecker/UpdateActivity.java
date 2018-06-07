package stonewall.osrs.namechecker;

import java.util.concurrent.Callable;

public class UpdateActivity implements Callable<Integer>
{
    private static int successfulChecks;

    public UpdateActivity()
    {
        successfulChecks = 0;
    }

    public UpdateActivity(int checks)
    {
        successfulChecks = checks;
    }

    @Override
    public Integer call() throws Exception
    {
        successfulChecks++;
        return successfulChecks;
    }
}
