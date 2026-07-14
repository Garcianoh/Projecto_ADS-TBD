package com.uan.dasoboleia.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Responsável exclusivamente por enviar emails.
 * Não conhece regras de negócio — apenas envia o que lhe é pedido.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String REMETENTE = "boleiauan@gmail.com";
    private static final String ASSUNTO = "Recuperação de Senha — Boleia UAN";

    private final JavaMailSender mailSender;

    @Async
    public void enviarCodigoRecuperacao(String destinatario, String nome, String codigo) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(REMETENTE);
            helper.setTo(destinatario);
            helper.setSubject(ASSUNTO);
            helper.setText(construirCorpoHtml(nome, codigo), true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar email de recuperação", e);
        }
    }

    private String construirCorpoHtml(String nome, String codigo) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:Arial, Helvetica, sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:32px 0;">
                        <tr>
                            <td align="center">
                                <table width="480" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.08);">
                                    <tr>
                                        <td style="background-color:#1e3a8a; padding:24px; text-align:center;">
                                            <span style="color:#ffffff; font-size:20px; font-weight:bold;">Boleia UAN</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:32px;">
                                            <p style="font-size:16px; color:#333333; margin:0 0 16px;">Olá %s,</p>
                                            <p style="font-size:15px; color:#555555; margin:0 0 24px; line-height:1.5;">
                                                Recebemos um pedido de recuperação de senha para a tua conta.
                                                Usa o código abaixo para continuar:
                                            </p>
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td align="center" style="padding:16px; background-color:#f0f4ff; border-radius:6px;">
                                                        <span style="font-size:28px; font-weight:bold; letter-spacing:6px; color:#1e3a8a;">%s</span>
                                                    </td>
                                                </tr>
                                            </table>
                                            <p style="font-size:13px; color:#888888; margin:24px 0 0; line-height:1.5;">
                                                Este código é válido por <strong>10 minutos</strong>.
                                                Se não foste tu que solicitaste esta recuperação, podes ignorar este email com segurança.
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color:#f4f4f7; padding:16px; text-align:center;">
                                            <span style="font-size:12px; color:#999999;">Equipa Boleia UAN — UAN, Luanda</span>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(nome, codigo);
    }

    @Async
    public void enviarNotificacaoBoleiaProxima(String destinatario, String nome,
                                                String origem, String destino,
                                                String dataInicio) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(REMETENTE);
            helper.setTo(destinatario);
            helper.setSubject("A tua boleia começa em 15 minutos — Boleia UAN");
            helper.setText(construirCorpoNotificacaoInicio(nome, origem, destino, dataInicio), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar notificação de boleia próxima", e);
        }
    }

    @Async
    public void enviarNotificacaoSemCondutor(String destinatario, String nome,
                                            String origem, String destino,
                                            String dataInicio) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(REMETENTE);
            helper.setTo(destinatario);
            helper.setSubject("⚠️ A tua boleia não tem condutor — Boleia UAN");
            helper.setText(construirCorpoNotificacaoSemCondutor(nome, origem, destino, dataInicio), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar notificação de boleia sem condutor", e);
        }
    }

    private String construirCorpoNotificacaoInicio(String nome, String origem,
                                                String destino, String dataInicio) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:Arial, sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:32px 0;">
                        <tr>
                            <td align="center">
                                <table width="480" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:8px; overflow:hidden;">
                                    <tr>
                                        <td style="background-color:#1e3a8a; padding:24px; text-align:center;">
                                            <span style="color:#ffffff; font-size:20px; font-weight:bold;">Boleia UAN</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:32px;">
                                            <p style="font-size:16px; color:#333333;">Olá %s,</p>
                                            <p style="font-size:15px; color:#555555; line-height:1.5;">
                                                A tua boleia de <strong>%s</strong> para <strong>%s</strong>
                                                começa em <strong>15 minutos</strong>!
                                            </p>
                                            <p style="font-size:14px; color:#888888;">Data: %s</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color:#f4f4f7; padding:16px; text-align:center;">
                                            <span style="font-size:12px; color:#999999;">Equipa Boleia UAN</span>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(nome, origem, destino, dataInicio);
    }

    private String construirCorpoNotificacaoSemCondutor(String nome, String origem,
                                                        String destino, String dataInicio) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:Arial, sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:32px 0;">
                        <tr>
                            <td align="center">
                                <table width="480" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:8px; overflow:hidden;">
                                    <tr>
                                        <td style="background-color:#dc2626; padding:24px; text-align:center;">
                                            <span style="color:#ffffff; font-size:20px; font-weight:bold;">⚠️ Boleia UAN</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:32px;">
                                            <p style="font-size:16px; color:#333333;">Olá %s,</p>
                                            <p style="font-size:15px; color:#555555; line-height:1.5;">
                                                A tua boleia de <strong>%s</strong> para <strong>%s</strong>
                                                começa em <strong>15 minutos</strong> e ainda
                                                <strong style="color:#dc2626;">não tem condutor inscrito</strong>.
                                            </p>
                                            <p style="font-size:15px; color:#555555;">
                                                Considera cancelar a boleia ou divulgar para encontrares um condutor.
                                            </p>
                                            <p style="font-size:14px; color:#888888;">Data: %s</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color:#f4f4f7; padding:16px; text-align:center;">
                                            <span style="font-size:12px; color:#999999;">Equipa Boleia UAN</span>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(nome, origem, destino, dataInicio);
    }
}