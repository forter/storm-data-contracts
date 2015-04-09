package com.forter.contracts;

import backtype.storm.topology.ReportedFailedException;
import com.forter.contracts.validation.ValidatedContract;

/**
 * A reusable adapter class between {@link com.forter.contracts.validation.ValidatedContract} and {@link java.lang.RuntimeException}
 */
public class ContractViolationReportedFailedException extends ReportedFailedException {
    private final Iterable<ValidatedContract> violations;
    private final String boltId;

    public ContractViolationReportedFailedException(Iterable<ValidatedContract> violations, String boltId) {
        super();
        this.violations = violations;
        this.boltId = boltId;
    }

    @Override
    public String getMessage() {
        // evaluate toString only when needed.
        return "There were some contract violations in '" + boltId + "' bolt." + getViolationsString(this.violations);
    }

    @Override
    public Throwable fillInStackTrace()
    {
        return this;
    }

    public static String getViolationsString(Iterable<ValidatedContract> violations) {
        StringBuilder sb = new StringBuilder("Detected violations were:\n - ");
        for (ValidatedContract vc : violations) {
            sb.append(vc.toString()).append("\n");
        }
        return sb.toString();
    }
}
