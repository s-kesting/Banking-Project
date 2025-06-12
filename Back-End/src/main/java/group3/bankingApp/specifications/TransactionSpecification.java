package group3.bankingApp.specifications;

import org.springframework.data.jpa.domain.Specification;

import group3.bankingApp.model.Transaction;
import jakarta.persistence.criteria.Predicate;

public class TransactionSpecification {

    public static Specification<Transaction> hasSenderOrReceiverIban(String iban) {
        return (root, query, criteriaBuilder) -> {
            if (iban == null || iban.isEmpty()) {
                return null;
            }

            // Assuming Transaction has sender and receiver relationships
            Predicate senderPredicate = criteriaBuilder.equal(
                    root.get("sender").get("iban"), iban);

            Predicate receiverPredicate = criteriaBuilder.equal(
                    root.get("receiver").get("iban"), iban);

            return criteriaBuilder.or(senderPredicate, receiverPredicate);
        };
    }

}
