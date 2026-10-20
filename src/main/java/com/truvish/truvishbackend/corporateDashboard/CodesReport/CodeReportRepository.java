package com.truvish.truvishbackend.corporateDashboard.CodesReport;

import com.truvish.truvishbackend.TruOpeAdmin.TruvishCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodeReportRepository
        extends JpaRepository<TruvishCode, Long> {



    List<TruvishCode>
    findByClientIdOrderByTruvishCodeTimestampDesc(
            Long clientId
    );



    Optional<TruvishCode>
    findByTruvishIdCodeNumber(
            String truvishIdCodeNumber
    );



    List<TruvishCode>
    findByClientIdAndTruvishCodeStatusOrderByTruvishCodeTimestampDesc(
            Long clientId,
            com.truvish.truvishbackend.TruOpeAdmin.VoucherStatus status
    );
}
