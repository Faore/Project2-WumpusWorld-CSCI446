package csci446.project2.Agents.Inference;

import java.util.ArrayList;

/**
 * Created by cetho on 10/20/2016.
 */
public class Rule {
    /*
    Rules are horn clauses
    https://en.wikipedia.org/wiki/Horn_clause

    Negating will negate the implication only.
    Format: ¬P(X) <= Q(Y) ^ T(Z) ...

    All variables are cells. Available predicates are in the Predicates Enum. If we need more, add them there.

    Fun facts:
    Alt+0172 = negation (¬)
    */

    public final Predicate implication;

    public final Predicate[] conditions;

    public final Quantifier quantifier;

    public final Variable quantifiedVariable;

    public Rule(Quantifier quantifier, Variable quantifiedVariable, Predicate implication, ArrayList<Predicate> conditions ) {
        this.quantifier = quantifier;
        this.quantifiedVariable = quantifiedVariable;
        this.implication = implication;
        this.conditions = (Predicate[]) conditions.toArray();
    }
}
