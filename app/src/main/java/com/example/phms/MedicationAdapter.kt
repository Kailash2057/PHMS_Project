package com.example.phms

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.TextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MedicationAdapter(private val context: Context) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    private var medications = mutableListOf<Medication>()

    fun setData(newList: List<Medication>) {
        medications.clear()
        medications.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.bind(medications[position])
    }

    override fun getItemCount() = medications.size

    inner class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.medicationName)
        private val dosageText: TextView = itemView.findViewById(R.id.medicationDosage)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEditMedication)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteMedication)

        fun bind(med: Medication) {
            nameText.text = med.name
            dosageText.text = "Dosage: ${med.dosage}"

            editButton.setOnClickListener {
                val intent = Intent(context, AddMedicationActivity::class.java)
                intent.putExtra("medicationId", med.id)
                intent.putExtra("username", med.username)
                context.startActivity(intent)
            }

            deleteButton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Delete Medication")
                    .setMessage("Are you sure you want to delete ${med.name}?")
                    .setPositiveButton("Yes") { _, _ ->
                        val db = MedicationDbHelper(context)
                        db.deleteMedication(med.name, med.username)
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            medications.removeAt(position)
                            notifyItemRemoved(position)
                        }
                        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            itemView.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(med.name)
                    .setMessage("""
                        Dosage: ${med.dosage}
                        Frequency: ${med.frequency} times per day
                        Start Date: ${med.startDate}
                        End Date: ${med.endDate}
                        Intake Time: ${med.intakeTime}
                        Reminder Set: ${if (med.reminderSet) "Yes" else "No"}
                    """.trimIndent())
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}